<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <style>
        body {
            font-size: 48px;
        }
        p{
            font-size: 13px;
            padding: auto;
        }
        table{
            border: 1px solid gray;
        }
        td{
            padding-left: 15px;
            padding-right: 15px;
        }
        #message{
            padding-left: 30px;
            padding-right: 30px;
            padding-bottom: -15px;
        }
        #footer{
            border-top: 5px solid rgb(4, 161, 235);
        }
    </style>
</head>
<body style="margin: 0; padding: 0;">

    <table align="center" cellpadding="0" cellspacing="0" width="600" style="border-collapse: collapse;">
        <tr>
            <td align="center" bgcolor="lightgrey" style="padding: 4px;">
                <img src="https://stage.oodleslab.com/assets/dashboard/front/logo.png" alt="Error on image load" style="display: block;" />
            </td>
        </tr>
        <tr>
            <td>
                <p style="padding-top: 10px; padding-bottom: 5px;">Hello <b>${username}, </b></p>

                <p id="message">We have start a new conversation in ${chatRoomName} group at <b>${creationDate}</b>.
                    <br>
                    You have <b>added</b> in <b>${chatRoomName}</b> group by ${creatorName}.
                </p>
    
                <br>
                <p>Thanks<br>
                ${creatorName}
                </p>
            </td>
        </tr>
        <tr id="footer">
            <td align="center" bgcolor="lightgrey">
                <p>Copyright 2009-2021 OodlesTechnologies. All rights reserved.</p>
            </td>
        </tr>
    </table>

</body>
</html>