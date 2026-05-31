let email=document.querySelector("#email")
let pass=document.querySelector("#password")
let form=document.querySelector("#loginForm")

form.addEventListener("submit",function(e){
    e.preventDefault();
    const emailRegex = /^[\w.-]+@[a-zA-Z\d.-]+\.[a-zA-Z]{2,}$/;
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
let ce=emailRegex.test(email.value);
let pe=passwordRegex.test(pass.value)
console.log(ce);
 document.querySelector(".error-message").textContent=""
 pass.parentElement.querySelector(".error-message").textContent=""
console.log(pe);
if(!ce){
   email.parentElement.querySelector(".error-message").textContent="Email format is not correct"
   
}
if(!pe){
    pass.parentElement.querySelector(".error-message").textContent="Password format is not correct"
}
if(ce && pe){
    alert("Submission Successful")
    email.value=""
    pass.value=""
}

})

