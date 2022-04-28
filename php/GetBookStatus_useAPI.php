<?php
    $connection = mysqli_connect("localhost", "yuninseon", "password", "yuninseon");
    mysqli_query($connection,'SET NAMES utf8');

    $userID = $_POST["userID"];

    // 파일 받기
    $img_File = $_FILES['uploaded_file']['name'];

    // 저장할 경로
    $save_path = $_SERVER['DOCUMENT_ROOT'].'/img/';
    $tempData = $_FILES['uploaded_file']['tmp_name'];
    $file_name = basename($_FILES["uploaded_file"]["name"]);

    $file_path = $save_path.$file_name;

    // // 임시폴더에서  ->  경로 이동 .파일이름
    if(isset($img_File)){
        // 옮길려고 하는 경로에 해당 이미지 파일이 이미 존재하면 지우고 옮김
        if(file_exists($save_path.$file_name)) {
            unlink($save_path.$file_name);
        }

        if(move_uploaded_file($tempData, $file_path)){
            echo "Success : Uploaded file in Server";
        }else{
            echo "Fail : Uploded file in Server";
        }

    }else{
        echo "Fail : have not file";
    }

    exec("cd ./api/ && python3 kakaoAPI_V3.py ".$file_path, $output);

    // json_encode 수정 필요함
    $result = array();

    for($output as $row) {
        // API 결과에 있는 ISBN 00000000에서 공백과 ISBN 제거
        $request_ISBN = str_replace(" ", "", $row);
        $request_ISBN = str_replace("ISBN", "", $request_ISBN);
        
        // DB Search
        $statement = mysqli_prepare($connection, "SELECT * FROM Book_Status WHERE ISBN = ?");
        mysqli_stmt_bind_param($statement, "s", $request_ISBN);
        mysqli_stmt_execute($statement);

        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $ISBN, $Name, $Writer, $Quentity);

        while(mysqli_stmt_fetch($statement)) {
            $result["ISBN"] = $ISBN;
            $result["Name"] = $Name;
            $result["Writer"] = $Writer;
            $result["Quentity"] = $Quentity;
        }
    }

    echo json_encode($result, JSON_UNESCAPED_UNICODE);
?>