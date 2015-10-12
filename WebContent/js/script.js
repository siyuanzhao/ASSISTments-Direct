/**
 * 
 */
function loadFacebook() {
	$.ajaxSetup({
		cache : true
	});
	$.getScript('//connect.facebook.net/en_US/sdk.js', function() {
			FB.init({
				//production appid id
				appId : '1599532550319029',
				//test1 appid id
//				appId : '1601551970117087',
//				csta14-5 appid id
//				appId : '1601556916783259',
				//com appid id
//				appId : '1604692316469719',
				xfbml : true,
				version : 'v2.3'
			});
		});
}

function renderButton() {
	gapi.signin2.render('my-signin2', {
		//'scope': 'https://www.googleapis.com/auth/plus.login',
		'scope' : 'email',
		'width' : 250,
		'height' : 50,
		'longtitle' : true,
		'theme' : 'dark',
		'onSuccess' : onSignIn
	});
}