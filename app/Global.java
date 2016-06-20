import play.GlobalSettings;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.Function0;

import java.util.concurrent.TimeUnit;

/**
 * Created by Corey on 6/20/2016.
 * Project: playsimplenewsfeed
 * <p></p>
 * Purpose of Class:
 */
public class Global extends GlobalSettings {

    @Override
    public F.Promise<Result> onBadRequest(Http.RequestHeader request, String error) {
        return F.Promise.delayed(null, 250L, TimeUnit.MILLISECONDS);
    }
}
