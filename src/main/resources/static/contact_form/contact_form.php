<?php
error_reporting(E_ALL & ~E_NOTICE);
require_once("config.php");
if(isset($_POST["action"]) && $_POST["action"]=="contact_form")
{
	ob_start();
	//contact form
	require_once("../phpMailer/src/Exception.php");
	require_once("../phpMailer/src/PHPMailer.php");
	require_once("../phpMailer/src/SMTP.php");
	$result = array();
	$result["isOk"] = true;
	if($_POST["name"]!="" && $_POST["name"]!=_def_name && $_POST["email"]!="" && $_POST["email"]!=_def_email && preg_match("#^[_a-zA-Z0-9-]+(\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*(\.[a-zA-Z]{2,12})$#", $_POST["email"]) && ((isset($_POST["form_type"]) && $_POST["form_type"]=="appointment") || ((!isset($_POST["form_type"]) || $_POST["form_type"]!="appointment") && $_POST["message"]!="" && $_POST["message"]!=_def_message)))
	{
		$values = array(
			"name" => $_POST["name"],
			"email" => $_POST["email"],
			"phone" => ($_POST["phone"]!=_def_phone ? $_POST["phone"] : ""),
			"message" => $_POST["message"],
			"message_appointment" => isset($_POST["message_appointment"]) ? $_POST["message_appointment"] : ''
		);
		if((bool)ini_get("magic_quotes_gpc")) 
			$values = array_map("stripslashes", $values);
		$values = array_map("htmlspecialchars", $values);
		
		$form_data = "";
		foreach($_POST as $key=>$value)
		{
			if(array_key_exists($key . "-label", $_POST))
			{
				if(array_key_exists($key . "-name", $_POST))
				{
					if(!empty($value))
						$form_data .= "<br>" . $_POST[$key . "-label"] . " " . $_POST[$key . "-name"] . " (" . $value . ")";
				}
				else
				{
					if(!empty($value))
						$form_data .= "<br>" . $_POST[$key . "-label"] . " " . $value;
				}
			}
		}

		$mail=new PHPMailer\PHPMailer\PHPMailer();

		$mail->CharSet='UTF-8';

		$mail->SetFrom((!empty(_from_email)? _from_email : _to_email), (!empty(_from_name)? _from_name : _to_name));
		$mail->AddAddress(_to_email, _to_name);
		$mail->AddReplyTo($values["email"], $values["name"]);

		$smtp=_smtp_host;
		if(!empty($smtp))
		{
			$mail->IsSMTP();
			$mail->SMTPAuth = true; 
			//$mail->SMTPDebug  = 2;
			$mail->Host = _smtp_host;
			$mail->Username = _smtp_username;
			$mail->Password = _smtp_password;
			if((int)_smtp_port>0)
				$mail->Port = (int)_smtp_port;
			$mail->SMTPSecure = _smtp_secure;
		}

		$mail->Subject = (isset($values["subject"]) && $values["subject"]!="" && $values["subject"]!=_subject_email ? $values["subject"] : _subject_email);
		$mail->MsgHTML(include("../contact_form/template.php"));

		if($mail->Send())
			$result["submit_message"] = _msg_send_ok;
		else
		{
			$result["isOk"] = false;
			$result["submit_message"] = _msg_send_error . (isset($mail->ErrorInfo) ? " " . $mail->ErrorInfo : "");
		}
	}
	else
	{
		$result["isOk"] = false;
		if($_POST["name"]=="" || $_POST["name"]==_def_name)
			$result["error_name"] = _msg_invalid_data_name;
		if($_POST["email"]=="" || $_POST["email"]==_def_email || !preg_match("#^[_a-zA-Z0-9-]+(\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*(\.[a-zA-Z]{2,12})$#", $_POST["email"]))
			$result["error_email"] = _msg_invalid_data_email;
		if($_POST["message"]=="" || $_POST["message"]==_def_message)
			$result["error_message"] = _msg_invalid_data_message;
	}
	$system_message = ob_get_clean();
	$result["system_message"] = $system_message;
	echo @json_encode($result);
	exit();
}
?>