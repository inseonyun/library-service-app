import numpy as np
import cv2

#Kako OCR api
import json
import requests
import sys
import re
from PIL import Image, ImageDraw

LIMIT_PX = 1024
LIMIT_BYTE = 1024*1024  # 1MB
LIMIT_BOX = 40

MYAPP_KEY = '여기에 API Key값 입력'

def kakao_ocr_resize(image_path: str):
    """
    ocr detect/recognize api helper
    ocr api의 제약사항이 넘어서는 이미지는 요청 이전에 전처리가 필요.

    pixel 제약사항 초과: resize
    용량 제약사항 초과  : 다른 포맷으로 압축, 이미지 분할 등의 처리 필요. (예제에서 제공하지 않음)

    :param image_path: 이미지파일 경로
    :return:
    """
    image = cv2.imread(image_path)
    height, width, _ = image.shape

    if LIMIT_PX < height or LIMIT_PX < width:
        ratio = float(LIMIT_PX) / max(height, width)
        image = cv2.resize(image, None, fx=ratio, fy=ratio)
        height, width, _ = height, width, _ = image.shape

        # api 사용전에 이미지가 resize된 경우, recognize시 resize된 결과를 사용해야함.
        image_path = "{}_resized.jpg".format(image_path)
        cv2.imwrite(image_path, image)

        return image_path
    return None


def kakao_ocr(image_path: str, appkey: str):
    """
    OCR api request example
    :param image_path: 이미지파일 경로
    :param appkey: 카카오 앱 REST API 키
    """
    API_URL = 'https://dapi.kakao.com/v2/vision/text/ocr'

    headers = {'Authorization': 'KakaoAK {}'.format(appkey)}

    image = cv2.imread(image_path)
    jpeg_image = cv2.imencode(".jpg", image)[1]
    data = jpeg_image.tobytes()


    return requests.post(API_URL, headers=headers, files={"image": data})

def main(file_path):
    image_path, appkey = file_path, MYAPP_KEY

    resize_impath = kakao_ocr_resize(image_path)
    if resize_impath is not None:
        image_path = resize_impath
        #print("원본 대신 리사이즈된 이미지를 사용합니다.")

    #카카오 API에서 범위, 인식한 글씨 받기
    output = kakao_ocr(image_path, appkey).json()
    outputdata = json.dumps(output, ensure_ascii=False,sort_keys=True, indent=2)
    #print("[OCR] output:\n{}\n".format(outputdata))

    #받은 데이터 array로 변환
    outputdata = json.loads(outputdata)
    #print(str(outputdata))

    if 'result' in outputdata :
      for i in range(len(outputdata['result'])):
        word = outputdata['result'][i]['recognition_words'][0]
        word = word.strip()
    
        x = outputdata['result'][i]['boxes'][0][0]
        y = outputdata['result'][i]['boxes'][0][1]
        w = outputdata['result'][i]['boxes'][1][0] - outputdata['result'][i]['boxes'][0][0]
        h = outputdata['result'][i]['boxes'][2][1] - outputdata['result'][i]['boxes'][0][1]

        # 원본 이미지
        org_img = cv2.imread(image_path)

        if word.find('ISBN') != -1:
          if re.search('\d', word) == None:
            
            nextX = outputdata['result'][i + 1]['boxes'][0][0]
            nextY = outputdata['result'][i + 1]['boxes'][0][1]
            nextW = outputdata['result'][i + 1]['boxes'][1][0] - outputdata['result'][i + 1]['boxes'][0][0]
            nextH = outputdata['result'][i + 1]['boxes'][2][1] - outputdata['result'][i + 1]['boxes'][0][1]

            # 자른 이미지
            cut_img = org_img[y:y+h, x:x+w]
            cut_img2 = org_img[y:nextY+nextH]

            # 자른 이미지 출력
            #cv2_imshow(cut_img)
            #cv2_imshow(cut_img2)
            word = outputdata['result'][i]['recognition_words'][0]
            if re.search('\d', outputdata['result'][i + 1]['recognition_words'][0]) == None:
              continue
            # 추출된 isbn에서 알파벳이 포함 되어 있으면 제거
            re_word = re.sub(r"[a-z]", "", outputdata['result'][i + 1]['recognition_words'][0])
            word += " : " + re_word
          else :
            # 자른 이미지
            cut_img = org_img[y:y+h, x:x+w]
            # 자른 이미지 출력
            #cv2_imshow(cut_img)

          # 양 끝에 하이폰 있으면 제거
          word = word.strip('-')
          print(word)

    #else:
       #print('사진 인식 실패')

if __name__ == "__main__":
    main(sys.argv[1])
