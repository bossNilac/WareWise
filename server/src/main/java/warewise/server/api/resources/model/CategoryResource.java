package warewise.server.api.resources.model;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import warewise.server.api.response.ApiResponse;
import warewise.server.common.handler.JsonSerializer;
import warewise.server.common.handler.CategoryHandler;
import warewise.server.common.model.Category;

/**
 * REST resource providing endpoints to manage categories.
 * <p>
 * Includes endpoints to retrieve categories,update ,add and delete categories.
 */
@Path("/categories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {
    /**
     * Retrieves all categories in the system.
     *
     * @return a {@link Response} containing all categories as JSON.
     */
    @GET
    @Path("/get_categories")
    public Response get_users() {
        String data = JsonSerializer.
                serializeListToJson(CategoryHandler.getInstance().getAllCategories());
        ApiResponse<String> resp = new ApiResponse<>(true, "Success", data);
        return Response.status(Response.Status.OK).entity(resp).build();
    }

    /**
     * Registers a new category with the provided data.
     * <p>
     *
     * @param req the {@link AddRequest} containing registration data.
     * @return a {@link Response} containing an {@link ApiResponse} indicating success or conflict.

     **/@POST
    @Path("/add_category")
    public Response addCategory(AddRequest req){
        for  (Category c: CategoryHandler.getInstance().getAllCategories ()){
            if(c.getName().equals(req.name)){
                ApiResponse<Void> resp = new ApiResponse<>(false, "Category already exists", null);
                return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
            }
        }

        Category newCategory = new Category(
                req.name,req.description
        );
        CategoryHandler.getInstance().addCategory(newCategory);
        ApiResponse<Void> resp = new ApiResponse<>(true, "Category added", null);
        return Response.status(Response.Status.OK).entity(resp).build();

    }

    /**
     * Updates an existing category.
     * Only provided (non-null) fields will be updated.
     * Validates that the category exists before updating.
     *
     * @param updateCategoryRequest the {@link UpdateCategoryRequest} with updated category data.
     * @return a {@link Response} indicating update result.
     */
    @PATCH
    @Path("/update_category")
    public Response update_user(UpdateCategoryRequest updateCategoryRequest) {
        if (updateCategoryRequest.categoryId == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "userId", null);
            return Response.status(Response.Status.BAD_REQUEST).entity(resp).build();
        }

        Category category = CategoryHandler.getInstance().getCategory(updateCategoryRequest.categoryId);
        if (category == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Category does not exist", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        }

        if (updateCategoryRequest.name != null) {
            category.setName(updateCategoryRequest.name);
        }
        if (updateCategoryRequest.description != null) {
            category.setDescription(updateCategoryRequest.description);
        }
        CategoryHandler.getInstance().updateCategory(category);

        ApiResponse<Void> resp = new ApiResponse<>(true, "Category updated!", null);
        return Response.status(Response.Status.OK).entity(resp).build();
    }


    /**
     * Deletes a category by ID.
     *
     * @param deleteCategoryRequest the {@link DeleteCategoryRequest} containing the category ID.
     * @return a {@link Response} indicating deletion result.
     */
    @DELETE
    @Path("/delete_category")
    public Response link_department(DeleteCategoryRequest deleteCategoryRequest){
        if(deleteCategoryRequest.categoryId ==null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "categoryId is needed", null);
            return Response.status(Response.Status.UNAUTHORIZED).entity(resp).build();
        }
        Category category = CategoryHandler.getInstance().getCategory(deleteCategoryRequest.categoryId) ;
        if (category == null) {
            ApiResponse<Void> resp = new ApiResponse<>(false, "Category not found", null);
            return Response.status(Response.Status.NOT_FOUND).entity(resp).build();
        } else {
            CategoryHandler.getInstance().deleteCategory(category.getID());
            ApiResponse<Void> resp = new ApiResponse<>(true, "Deleted category!", null);
            return Response.status(Response.Status.OK).entity(resp).build();
        }
    }


    static class DeleteCategoryRequest{
        public Integer categoryId;
    }

    /**
     * DTO for updating an existing category.
     */
    static class UpdateCategoryRequest {
        public Integer categoryId;
        public String name;
        public String description;
    }

    static class AddRequest {
        public String name;
        public String description;
    }
}