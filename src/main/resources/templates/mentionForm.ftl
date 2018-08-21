<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>글 쓰기</title>
    <link rel="stylesheet" href="https://unpkg.com/purecss@1.0.0/build/pure-min.css" integrity="sha384-nn4HPE8lTHyVtfCBi5yW9d20FjT8BJwUXyWZT9InLYax14RDjBj46LmSztkmNP9w" crossorigin="anonymous">
    <!--[if lte IE 8]>
    <link rel="stylesheet" href="https://unpkg.com/purecss@1.0.0/build/grids-responsive-old-ie-min.css">
    <![endif]-->
    <!--[if gt IE 8]><!-->
    <link rel="stylesheet" href="https://unpkg.com/purecss@1.0.0/build/grids-responsive-min.css">
    <!--<![endif]-->
    <style>
        .pad-1 {
            padding: 1em;
        }
    </style>
</head>
<body onload="initFocus()">
<div class="pure-g">
    <form class="pure-form pure-form-stacked" action="/oauth/mentions" method="post">
        <div class="pure-u-1">
            <div class="pad-1">
                <legend>트위터에 남길 글을 써 주세요</legend>
            </div>
            <div class="pad-1">
                <textarea id="mention" name="mention" cols="50" rows="5" placeholder="140자 이내로 써 주세요"></textarea>
            </div>
            <div class="pad-1">
                <button type="submit" class="pure-button pure-button-primary">트위터에 남기기</button>
            </div>
        </div>
    </form>
</div>
<script>
function initFocus() {
    document.getElementById('mention').focus();
}
</script>
</body>
</html>