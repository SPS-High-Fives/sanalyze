$(document).ready(function() {
  $('#input-text-options').on('change',function() {
    var inputType = $(this).val();
    console.log(inputType);
    if (inputType == "file") {
      $('#input-text').hide();
      $('#file-selector').show();
    } else {
      $('#input-text').show();
      $('#file-selector').hide();
    }
  });
});
