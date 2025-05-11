package customers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer handleGetCustomer(@PathParam("uuid") UUID uuid) {
        return customerService.getCustomer(uuid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> handleGetAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String handleUpdateCustomer(Customer customer) {
        return customerService.updateCustomer(customer);
    }

    @DELETE
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String handleDeleteCustomer(@PathParam("uuid") UUID uuid) {
        return customerService.deleteCustomer(uuid);
    }
}
