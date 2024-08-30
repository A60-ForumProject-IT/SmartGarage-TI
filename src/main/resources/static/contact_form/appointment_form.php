<?php
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["action"]) && $_POST["action"] == "appointment_form") {

    $url = 'http://localhost:8080/api/appointment';

    // Събиране на данните от формата
    $name = trim($_POST["name"]);
    $email = trim($_POST["email"]);
    $phone = trim($_POST["phone"]);
    $vehicleYear = $_POST["vehicle-year"]; // Годината на автомобила, избрана с drag and drop
    $vehicleMake = $_POST["vehicle-make"]; // Марката на автомобила, избрана от dropdown
    $vehicleMileage = $_POST["vehicle-mileage"]; // Километраж
    $appointmentDate = $_POST["appointment-date"]; // Дата на запазване
    $timeFrame = $_POST["time-frame"]; // Предпочитан часови интервал
    $services = isset($_POST["services"]) ? implode(", ", $_POST["services"]) : ""; // Услугите, избрани с checkbox
    $message = trim($_POST["message_appointment"]); // Допълнителни въпроси или коментари

    // Валидация на данните (пример)
    $errors = array();
    if (empty($name)) {
        $errors[] = "Please enter your name.";
    }
    if (empty($email) || !filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $errors[] = "Please enter a valid email address.";
    }
    if (empty($appointmentDate)) {
        $errors[] = "Please select an appointment date.";
    }
    if (empty($vehicleYear)) {
        $errors[] = "Please select the vehicle year.";
    }
    if (empty($vehicleMake)) {
        $errors[] = "Please select the vehicle make.";
    }

    if (!empty($errors)) {
        echo json_encode(array("isOk" => false, "errors" => $errors));
        exit();
    }

    // Подготвяне на данните за изпращане към Spring Boot
    $data = array(
        'name' => $name,
        'email' => $email,
        'phone' => $phone,
        'vehicleYear' => $vehicleYear,
        'vehicleMake' => $vehicleMake,
        'vehicleMileage' => $vehicleMileage,
        'appointmentDate' => $appointmentDate,
        'timeFrame' => $timeFrame,
        'services' => $services,
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

    if ($result === FALSE) {
        echo json_encode(array("isOk" => false, "submit_message" => "Failed to send appointment request."));
    } else {
        echo json_encode(array("isOk" => true, "submit_message" => "Appointment request sent successfully!"));
    }

    exit();
}
?>
