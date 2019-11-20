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

if(isset($_POST['idFuncionario']) ){
	$idFuncionario = $_POST['idFuncionario'];
	
	$query = mysqli_query($conn,"select * from ponto where idFuncionario = $idFuncionario order by horario desc limit 1");
	
	if($query){
	
		while($row = mysqli_fetch_array($query)){
			$flag[]=$row;
		}
		print(json_encode($flag));

		
	}else{
		 echo "failed to query";
	}


}

mysqli_close($conn);
?>