<#assign content>
<div id="create_sesh">
<form method="POST" action="/create/party">
<label>Sesh Name: <input type="text" id="sesh_name" autocomplete="off" required></label> 
<label>Host Name: <input type="text" id="host_name" autocomplete="off"></label>
<div id="privacy_settings">
<label>Private <input type="radio" name="privacy_setting" value="public" id="private_sesh" required></label>
<label>Public <input type="radio" name="privacy_setting" value="private" id="public_sesh" required></label> 
</div>

<input type="submit" value="Submit">
</form>
</div>
</#assign>
<#include "main.ftl">