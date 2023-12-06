
$(document).ready(function(){
   // alert("dharam");
	
});

const searchByKey =()=>{
	console.log("searching");
	var query=$("#search-input").val();

	if(!(query=='')){
		
		var url='http://localhost:8080/search/'+query;
		fetch(url).then((response)=>{
			return response.json();
		}).then((data) =>{
			console.log(data);
			var text=`<div class="list-group">`;
			 data.forEach(contact =>{
				 
				 text+=`<a href='/user/${contact.cid}/contact' class='list-group-item list-group-action'>${contact.fName} ${contact.lastName}</a>`
			 });
			 text+=`</div>`;
			 $(".search-result").html(text);
			 $(".search-result").show();
		});
	}else{
		$(".search-result").hide();
	}
}
