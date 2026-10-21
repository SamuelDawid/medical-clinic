package com.medicalclinic.adapter.user;


import com.medicalclinic.adapter.page.PageDto;
import enums.SortDirection;
import internalCommands.PageQuery;
import internalCommands.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import user.UserDto;
import user.UserService;
import user.UserServiceImp;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations for managing User")
public class UserController {
    private final UserService userService;
    private final UserWebMapper mapper;

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Return all users")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public PageDto<UserDto> findAll(@ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        PageResult<User> result = userService.findAll(toPageQuerry(pageable));
        return
    }

    @Operation(description = "Get User By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public UserDto findById(@NonNull @PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(description = "Create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid user data")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto create(@RequestBody CreateUserCommand command) {
        return userService.create(command);
    }

    @Operation(description = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{id}/password")
    public void changePassword(@PathVariable Long id, @RequestBody ChangePasswordCommand command) {
        userService.changePassword(id, command);
    }

    @Operation(description = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    private PageQuery toPageQuerry(Pageable pageable){
        Sort.Order order = pageable.getSort().stream().findFirst().orElse(Sort.Order.asc("id"));
        return new PageQuery(pageable.getPageNumber(), pageable.getPageSize(),
                order.getProperty(), SortDirection.valueOf(order.getDirection().name()));
    }
}
