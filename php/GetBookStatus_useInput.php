<?php
    $connection = mysqli_connect("localhost", "ID", "password", "schema");
    mysqli_query($connection,'SET NAMES utf8');

    // input 받기
    $input_ISBN = $_POST['input'];

    if(empty($input_ISBN)) {
        echo "input Data is Empty";
    }else {
        // json_encode
        $result = array();

        $request_ISBN = "%".$input_ISBN."%";

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

        // 중복 데이터 제거
        $result = array_map("unserialize", array_unique(array_map("serialize", $result)));

        echo json_encode($result, JSON_UNESCAPED_UNICODE);
    }
    
?>