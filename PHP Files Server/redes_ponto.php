<?php
$db = "sucko467_redes";
//$servername = "br390.hostgator.com.br:3306";
$servername = "localhost";
$username = "sucko467_redes";
$password = "redesmoveis";


// Create connection
$conn = new mysqli($servername, $username, $password,$db);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

if(isset($_POST['idFuncionario']) and isset($_POST['macAddress']) and isset($_POST['aberto'])){
	$idFuncionario = $_POST['idFuncionario'];
	$macAddress = $_POST['macAddress'];
	$aberto = $_POST['aberto'];
	
	$query = mysqli_query($conn, "SELECT idDispositivo FROM dispositivo where macAddress = '$macAddress'");
			
	if($query){
			
		$row = mysqli_fetch_array($query, MYSQL_ASSOC);
		$row = array_map('utf8_encode', $row);
		$idDispositivo = (int) $row['idDispositivo'] + 1;
		
		$query = mysqli_query($conn,"insert into ponto (idFuncionario, idDispositivo, aberto, horario) values 
							($idFuncionario,$idDispositivo,$aberto,CURRENT_TIMESTAMP )");
	
		if($query){
			
		}else{
			 echo "failed to query";
		}
	
	}else{
		 echo "failed to query";
	}
	

}

mysqli_close($conn);
?>