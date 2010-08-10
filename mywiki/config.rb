Title = 'My Personal Wiki'
OutputDir = 'generated'

# JS for all pages
JSIndex = <<'js_index'
<script type="text/javascript">
$(document).ready(function(){

$('.title').click(function(event) {
    event.preventDefault();
    tag = '#div' + this.id
    if ($(tag).is(':hidden'))
        $(tag).show("fast")
    else
        $(tag).hide("fast")
})

})

function closeall() {
  $('.title').each (function(index, domElement) {
    tag = '#div' + this.id
    $(tag).hide()
  })
  return false
}

function toggleall() {
  $('.title').each (function(index, domElement) {
    tag = '#div' + this.id
    if ($(tag).is(':hidden'))
        $(tag).show("fast")
    else
        $(tag).hide("fast")
  })
  return false
}  
</script>
js_index

#| <a href="index.html">Home</a> | <a href='#' onclick='return toggleall()'>Toggle All</a> | <a href='#' onclick='return closeall()'>Close All</a> |<p>
#<script type="text/javascript" src="jquery.js"></script>
#{JSIndex}

# header of the html
Header = <<"head_part"
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf8">
<link rel="stylesheet" href="main.css" type="text/css">
</head><body>
<div class="content">
head_part

# footer of the html
Footer = <<'footer_part'
</div>
</body></html>
footer_part

# header for index page
IndexHead = <<"index_head"
<center><p class="indextitle">#{Title}</p></center>
index_head