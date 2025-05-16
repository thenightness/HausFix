package readings;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import customers.CustomerRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modules.IReading;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.ResponseUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("readings")
public class ReadingController {
    public static ReadingService readingService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleCreateReading(CreateableReading reading) {
        Reading read = readingService.getReading(readingService.createReading(reading)) ;
        return Response.status(Response.Status.CREATED).entity(read).build();
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
    public String handleUpdateReading(CreateableReading reading) {
        return readingService.updateReading(reading);
    }

    @DELETE
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String handleDeleteReading(@PathParam("uuid") UUID uuid) {
        return readingService.deleteReading(uuid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleFilteredReadings(
            @QueryParam("customer") UUID customerId,
            @QueryParam("start") String startStr,
            @QueryParam("end") String endStr,
            @QueryParam("kindOfMeter") String kindOfMeterStr) {

        try {
            LocalDate start = null;
            LocalDate end = null;

            if (startStr != null && !startStr.isBlank()) {
                start = LocalDate.parse(startStr);
            }

            if (endStr != null && !endStr.isBlank()) {
                end = LocalDate.parse(endStr);
            } else if (start != null) {
                end = LocalDate.now();
            }

            IReading.KindOfMeter kindOfMeter = null;
            if (kindOfMeterStr != null && !kindOfMeterStr.isBlank()) {
                kindOfMeter = IReading.KindOfMeter.valueOf(kindOfMeterStr);
            }

            List<Reading> readings = readingService.getFilteredReadings(customerId, start, end, kindOfMeter);
            return Response.ok(readings).build();

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Ungültige Anfrage oder fehlerhafte Daten: " + e.getMessage()).build();
        }
    }

}
