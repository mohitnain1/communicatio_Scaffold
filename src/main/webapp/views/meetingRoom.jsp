<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
	<link rel="styleSheet" href="css/style.css" type="text/css" media="screen">
	
	<script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"></script>
    <title>Meeting Started</title>
  </head>
 <body onload="" class="bg-light">
  	<div class="container-fluid p-1">
  		<div class="row text-center">
			<div class="col-2 border border-secondary bg-secondary">
				<div id="room" style="">
					
				</div>
			</div>
			<% 
  				String username = (String)request.getAttribute("username");
  				
  			%>
			<div class="col-4">
				<div class="card " style="height: 34rem;">
					<div class="card-header ">
						<b><a id="username"><%=username %></a> Text Chat Here!</b>
						<button onclick="connect()" class="accent username-submit">connect</button>
					</div>
					<div class="card-body">
						<ul id="messageArea">
						</ul>
					</div>
					<div class="card-footer">
							<div class=row>
								<div class="col-6">
									<label for="comment">Write your Message :</label>
								</div>
								<div class="col-6">
								<input type="file" class="form-control form-control-sm">
								</div>
							</div>
							<input class="form-control" id="message"></input>
							<button onclick="send()" id="messageSend" class="form-control btn btn-primary">Send</button>
					</div>
				</div>
			</div>
		</div>
		<div class="row fixed-bottom bg-light p-3 text-center">
			<div class="col-8">
				<button id="button-lea" class="btn btn-danger" onclick="leaveoom();" value="Leave room">Leave Meeting</button>
			</div>
			<div class="col-4">
			</div>
			
		</div>
  	</div>
  	<script src="js/main.js"></script>
  	<script	src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.1.4/sockjs.min.js"></script>
	<script	src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" ></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>
    
  </body>
</html>