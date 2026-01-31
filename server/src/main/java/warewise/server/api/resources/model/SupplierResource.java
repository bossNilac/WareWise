package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.handler.SupplierHandler;
import warewise.server.common.model.Supplier;

/**
 * REST resource providing endpoints to manage suppliers.
 * Includes endpoints to retrieve, add, update, and delete suppliers.
 */
@Path("/suppliers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SupplierResource {

    @GET
    @Path("/get_suppliers")
    public Response getSuppliers() {
        String data = JsonSerializer.serializeListToJson(
                SupplierHandler.getInstance().getAllSuppliers());
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @POST
    @Path("/add_supplier")
    public Response addSupplier(AddRequest req) {
        Supplier newSupplier = new Supplier(
                req.supplierName,
                req.contactEmail,
                req.contactPhone,
                req.address,
                req.createdAt
        );
        SupplierHandler.getInstance().addSupplier(newSupplier);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Supplier added", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @PATCH
    @Path("/update_supplier")
    public Response updateSupplier(UpdateRequest req) {
        if (req.supplierId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "supplierId required", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        Supplier supplier = SupplierHandler.getInstance().getSupplier(req.supplierId);
        if (supplier == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Supplier not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (req.supplierName != null) supplier.setName(req.supplierName);
        if (req.contactEmail != null) supplier.setContactEmail(req.contactEmail);
        if (req.contactPhone != null) supplier.setContactPhoneNo(req.contactPhone);
        if (req.address != null) supplier.setAddress(req.address);
        if (req.createdAt != null) supplier.setCreatedAt(req.createdAt);

        SupplierHandler.getInstance().updateSupplier(supplier);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Supplier updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    @DELETE
    @Path("/delete_supplier/{supplierId}")
    public Response deleteSupplier(@PathParam("supplierId") int supplierId) {

        Supplier supplier = SupplierHandler.getInstance().getSupplier(supplierId);
        if (supplier == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Supplier not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        SupplierHandler.getInstance().deleteSupplier(supplierId);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Supplier deleted", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    static class AddRequest {
        public String supplierName;
        public String contactEmail;
        public String contactPhone;
        public String address;
        public String createdAt;
    }

    static class UpdateRequest {
        public Integer supplierId;
        public String supplierName;
        public String contactEmail;
        public String contactPhone;
        public String address;
        public String createdAt;
    }
}
