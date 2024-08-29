<?php
ob_start();
?>
<html>
	<head>
	</head>
	<body>
		<div><b>Name</b>: <?php echo $values["name"]; ?></div>
		<div><b>E-mail</b>: <?php echo $values["email"]; ?></div>
		<?php if($values["phone"]!=""):?>
		<div><b>Phone</b>: <?php echo $values["phone"]; ?></div>
		<?php endif; ?>
		<?php if($values["message"]!=""):?>
		<div><b>Message</b>: <?php echo nl2br($values["message"]); ?></div>
		<?php endif; ?>
		<?php if($values["message_appointment"]!=""):?>
		<div><b>Message</b>: <?php echo nl2br($values["message_appointment"]); ?></div>
		<?php endif; ?>
		<?php echo $form_data; ?>
	</body>
</html>
<?php
$content = ob_get_contents();
ob_end_clean();
return($content);
?>	