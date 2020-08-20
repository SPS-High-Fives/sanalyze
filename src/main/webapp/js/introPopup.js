if(localStorage.getItem("sanalyzeIntro")==null){
   showIntro();
   localStorage.setItem("sanalyzeIntro","done");
}


function showIntro(){

    $("#Intro").modal("show");
}
