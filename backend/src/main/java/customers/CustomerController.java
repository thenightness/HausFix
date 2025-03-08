package customers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Path("customers")
public class CustomerController {
    public static CustomerService customerService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleCreateCustomer(Customer customer) {
        customerService.createCustomer(customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String handleUpdateCustomer(Customer customer) {
        return customerService.updateCustomer(customer);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> handleGetAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer handleGetCustomer(@PathParam("uuid") UUID uuid) {
        return customerService.getCustomer(uuid);
    }

    @DELETE
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String handleDeleteCustomer(@PathParam("uuid") UUID uuid) {
        return customerService.deleteCustomer(uuid);
    }
}
