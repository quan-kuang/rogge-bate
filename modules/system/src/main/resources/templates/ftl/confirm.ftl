<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="icon" type="image/x-icon" href="/static/favicon/favicon.ico">
    <script src="/static/js/jquery-3.4.1.min.js"></script>
</head>
<body>
<h1 style="text-align: center">${apiResult.msg}</h1>
<script type="text/javascript">
    if (${apiResult.flag?string('true','false')}) {
        window.location.href = "${apiResult.data}";
    }
</script>
</body>
</html>