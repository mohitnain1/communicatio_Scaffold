<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
    <title>index</title>
    <script type="text/javascript" src="js/home.js"></script>
  </head>
 <body class="bg-secondary mt-5">
  
  	<div class="container col d-flex justify-content-center ">
  		<div class="card mt-5 p-2" style="width: 30rem;">
  			<div class="card-body">
  				<div class="row">
		  			<div class="col-12">
		  				<h3 class="text-center" style="font-family: Purisa; color: blue;"><b>Communication Scaffold</b></h3>
		  			</div>
		  		</div>
		  		<hr>
		  		<hr>
		  		<div class="row pt-1">
		  			<div class="col-12">
						<form action="/joinMeeting">
							<div class="form-group">
								<label for="name" style="font-family: Purisa; color: blue;">
								<b>Enter your name : </b>
								</label>
									<input oninput="enableJoin();" type="text" id="username" name="username" required="required" 
									class=" form-control"id="name" style="text-align: center;" />
									<br>
									<label for="name" style="font-family: Purisa; color: blue;">
									<b>Enter meeting name :</b>
									</label>
									<input type="text" id="roomName" name="roomName" required="required" 
									class=" form-control" style="text-align: center;" />
							</div>
							<br>
							<div class="text-center">
								<button id="join" class="btn btn-primary" type="submit"	
								disabled="disabled">Join Meeting</button>
							</div>
						</form>
					</div>
		  		</div>
  			</div>
  		</div>
  	</div>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" ></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
  </body>
</html>