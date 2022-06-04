<?php
    $connection = mysqli_connect("localhost", "ID", "password", "schema");
    mysqli_query($connection,'SET NAMES utf8');

    // 파일 받기
    $img_File = $_FILES['uploaded_file']['name'];

    // 저장할 경로
    $save_path = $_SERVER['DOCUMENT_ROOT'].'/photos/';
    $tempData = $_FILES['uploaded_file']['tmp_name'];
    $file_name = basename($_FILES["uploaded_file"]["name"]);

    $file_path = $save_path.$file_name;

    // // 임시폴더에서  ->  경로 이동 .파일이름
    if(isset($img_File)){
        // 옮길려고 하는 경로에 해당 이미지 파일이 이미 존재하면 지우고 옮김
        if(file_exists($file_path)) {
            unlink($file_path);
        }

        if(move_uploaded_file($tempData, $file_path)){
            echo "Success : Uploaded file in Server";
        }else{
            echo "Fail : Uploded file in Server";
        }

    }else{
        echo "Fail : have not file";
    }

    exec("cd ./api/ && python kakaoAPI_V3.py ".$file_path, $output);

    // json_encode 수정 필요함
    $result = array();

    if($output != null) {
        foreach($output as $row) {
            $request_ISBN = "";
            if (strpos($row, ":") == true) {
                $request_ISBN = explode(":", $row)[1];
                $request_ISBN = str_replace(" ", "", $request_ISBN);
            } else {
                $request_ISBN = str_replace(" ", "", $row);
                $request_ISBN = str_replace("ISBN", "", $request_ISBN);
            }
            $request_ISBN = str_replace("-", "", $request_ISBN);
            $request_ISBN = "%".$request_ISBN."%";

            // DB Search
            $statement = mysqli_prepare($connection, "SELECT * FROM book_status WHERE ISBN LIKE ?");
            mysqli_stmt_bind_param($statement, "s", $request_ISBN);
            mysqli_stmt_execute($statement);

            mysqli_stmt_store_result($statement);
            mysqli_stmt_bind_result($statement, $BookName, $ISBN, $Writer, $Quantity);

            $result_row = array();
            while(mysqli_stmt_fetch($statement)) {
                $result_row["BookName"] = $BookName;
                $result_row["ISBN"] = $ISBN;
                $result_row["Writer"] = $Writer;
                $result_row["Quantity"] = $Quantity;

                array_push($result, $result_row);
            }
        }

        // 중복 데이터 제거
        $result = array_map("unserialize", array_unique(array_map("serialize", $result)));
    }

    echo json_encode($result, JSON_UNESCAPED_UNICODE);
?>