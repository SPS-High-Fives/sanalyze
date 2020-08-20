
//display the intro modal when the user visits for the first time from a device
if(localStorage.getItem("sanalyzeIntro")==null){
  showIntro();
  localStorage.setItem("sanalyzeIntro","done");
}


//display the intro/info modal
function showIntro(){
  $("#Intro").modal("show");
}
