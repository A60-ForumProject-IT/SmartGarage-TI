<?php
error_reporting(E_ALL & ~E_NOTICE);

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["action"]) && $_POST["action"] == "contact_form") {

    $url = 'http://localhost:8080/api/contact';

    $name = trim($_POST["name"]);
    $email = trim($_POST["email"]);
    $phone = trim($_POST["phone"]);
    $message = trim($_POST["message"]);

    $errors = array();
    if (empty($name)) {
        $errors[] = "Please enter your name.";
    }
    if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $errors[] = "Please enter a valid email address.";
    }
    if (empty($message)) {
        $errors[] = "Please enter your message.";
    }

    if (!empty($errors)) {
        echo json_encode(array("isOk" => false, "errors" => $errors));
        exit();
    }

    $data = array(
        'name' => $name,
        'email' => $email,
        'phone' => $phone,
        'message' => $message
    );

    // Опции за HTTP заявката
    $options = array(
        'http' => array(
            'header'  => "Content-Type: application/json\r\n",
            'method'  => 'POST',
            'content' => json_encode($data),
        ),
    );

    // Изпращане на заявката към Spring Boot
    $context  = stream_context_create($options);
    $result = file_get_contents($url, false, $context);

    // Проверка за резултат от заявката
    if ($result === FALSE) {
        echo json_encode(array("isOk" => false, "submit_message" => "Failed to send message."));
    } else {
        echo json_encode(array("isOk" => true, "submit_message" => "Message sent successfully!"));
    }

    exit();
}
?>
