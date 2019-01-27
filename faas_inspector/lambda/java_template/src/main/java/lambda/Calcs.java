/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lambda;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import faasinspector.register;
import java.nio.charset.Charset;
import java.util.Random;
/**
 * lambda.Calcs::handleRequest
 * @author wlloyd
 */
public class Calcs implements RequestHandler<Request, Response>
{
    static String CONTAINER_ID = "/tmp/container-id";
    static Charset CHARSET = Charset.forName("US-ASCII");
    
    
    // Lambda Function Handler
    public Response handleRequest(Request request, Context context) {
        // Create logger
        LambdaLogger logger = context.getLogger();
        
        // Register function
        register reg = new register(logger);

        //stamp container with uuid
        Response r = reg.StampContainer();
        
        // *********************************************************************
        // Implement Lambda Function Here
        // *********************************************************************
        String hello = "No output";
        int calcs = request.getCalcs();
        int sleep = request.getSleep();
        int loops = request.getLoops();

        if (loops>0)
        {
            for (int i=0;i<request.getLoops();i++)
            {
                randomMath(request.getCalcs());
                try
                {
                    Thread.sleep(request.getSleep());
                }
                catch (InterruptedException ie)
                {
                    System.out.println("Sleep was interrupted - calc mode...");
                }  
            }
        }
        else  // sleep only - no calcs
        {
            try
            {
                Thread.sleep(request.getSleep());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Sleep was interrupted - no calc mode...");
            }
        }
        
        
        //Print log information to the Lambda log as needed
        //logger.log("log message...");
        
        // Set return result in Response class, class is marshalled into JSON
        r.setValue(hello);
        
        return r;
    }
    
    private void randomMath(int calcs)
    {
        Random rand = new Random();
        // By not reusing the same variables in the calc, this should prevent
        // compiler optimization... Also each math operation should operate
        // on between operands in different memory locations.
        long[] operand_a = new long[calcs];
        long[] operand_b = new long[calcs];
        long[] operand_c = new long[calcs];
        long mult;
        double div1;
        
        for (int i=0;i<calcs;i++)
        {
            // By not using sequential locations in the array, we should 
            // reduce memory lookup efficiency
            int j = rand.nextInt(calcs);
            operand_a[j] = rand.nextInt(99999);
            operand_b[j] = rand.nextInt(99999);
            operand_c[j] = rand.nextInt(99999);
            mult = operand_a[j] * operand_b[j];
            div1 = (double) mult / (double) operand_c[j];
        }
    }
    
    // int main enables testing function from cmd line
    public static void main (String[] args)
    {
        Context c = new Context() {
            @Override
            public String getAwsRequestId() {
                return "";
            }

            @Override
            public String getLogGroupName() {
                return "";
            }

            @Override
            public String getLogStreamName() {
                return "";
            }

            @Override
            public String getFunctionName() {
                return "";
            }

            @Override
            public String getFunctionVersion() {
                return "";
            }

            @Override
            public String getInvokedFunctionArn() {
                return "";
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return new LambdaLogger() {
                    @Override
                    public void log(String string) {
                        System.out.println("LOG:" + string);
                    }
                };
            }
        };
        
        // Create an instance of the class
        Calcs lt = new Calcs();
        
        // Create a request object
        Request req = new Request();
        
        // Grab the name from the cmdline from arg 0
        String name = (args.length > 0 ? args[0] : "");
        
        // Load the name into the request object
        req.setCalcs(1000000);
        req.setSleep(0);
        req.setLoops(25);

        // Report name to stdout
        System.out.println("cmd-line calcs=" + req.getCalcs() + " sleep=" + req.getSleep() + " loops=" + req.getLoops());
        
        // Run the function
        Response resp = lt.handleRequest(req, c);
        
        // Print out function result
        System.out.println("function result:" + resp.toString());
    }
}
