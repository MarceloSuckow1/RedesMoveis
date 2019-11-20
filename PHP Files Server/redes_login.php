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
    //die("Connection failed: " . $conn->connect_error);
} 

if(isset($_POST['idFuncionario']) and isset($_POST['senha']) and isset($_POST['macAddress']) ){
	$idFuncionario = $_POST['idFuncionario'];
	$senha = $_POST['senha'];
	$macAddress = $_POST['macAddress'];
	
	$query = mysqli_query($conn,"SELECT * FROM funcionario where idFuncionario=$idFuncionario and senha=$senha");
	
	if($query){
	
		while($row = mysqli_fetch_array($query)){
			$flag[]=$row;
		}
		print(json_encode($flag));

		$query = mysqli_query($conn,"INSERT INTO dispositivo (idFuncionario, macAddress)
			SELECT * FROM (SELECT $idFuncionario, '$macAddress') AS tmp
			WHERE NOT EXISTS (
    			SELECT idDispositivo FROM dispositivo WHERE macAddress = '$macAddress' and idFuncionario = $idFuncionario) LIMIT 1;");
    			
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