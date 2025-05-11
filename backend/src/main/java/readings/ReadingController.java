package readings;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import customers.CustomerRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.ResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Path("readings")
public class ReadingController {
    public static ReadingService readingService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleCreateReading(Reading reading) {
        readingService.createReading(reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Reading handleGetReading(@PathParam("uuid") UUID readingId) {
        return readingService.getReading(readingId);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String handleUpdateReading(Reading reading) {
        return readingService.updateReading(reading);
    }

    @DELETE
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String handleDeleteReading(@PathParam("uuid") UUID uuid) {
        return readingService.deleteReading(uuid);
    }
}
