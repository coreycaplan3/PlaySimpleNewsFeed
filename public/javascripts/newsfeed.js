/**
 * Created by Corey on 6/19/2016.
 *
 */
function onLikeClicked(isLiked) {
    if (!isLiked) {
        document.getElementById("likeButton").style.background = "red";
    } else {
        document.getElementById("likeButton").style.background = "white";
    }
}