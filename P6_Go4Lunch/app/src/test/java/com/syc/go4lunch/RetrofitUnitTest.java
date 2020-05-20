package com.syc.go4lunch;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import android.content.Context;
import org.junit.Before;
import org.mockito.Mockito;
import retrofit2.Retrofit;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RetrofitUnitTest {
    private final Context context = Mockito.mock(Context.class);
    //private GetNewsDataService mNYTService;
    Retrofit mRetrofit;
    private com.syc.go4lunch.utils.RetrofitInstance RetrofitInstance;

    @Before
    public void setUp() {
        mRetrofit = RetrofitInstance.getRetrofitInstance();
        //mNYTService = mRetrofit.create(GetNewsDataService.class);
    }

    /**
     * Test the Retrofit base call on the URL
     */
    @Test
    public void setRetrofitTest() {
        assertEquals("https://api.nytimes.com/svc/", mRetrofit.baseUrl().toString());
        assertNotNull(mRetrofit);
        assertTrue(mRetrofit.baseUrl().isHttps());
    }

}
