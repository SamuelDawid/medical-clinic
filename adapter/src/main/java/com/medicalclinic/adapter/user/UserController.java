package com.medicalclinic.adapter.user;


import com.medicalclinic.adapter.AdapterControllerCommon;
import common.ChangePasswordCommand;
import internalCommandsAndDtos.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import models.UserPOJO;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import page.PageDto;
import user.CreateUserCommand;
import user.UserDto;
import user.UserService;

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
        PageResult<UserPOJO> result = userService.findAll(AdapterControllerCommon.toPageQuerry(pageable));
        return PageDto.from(result.map(mapper::toDto));
    }

    @Operation(description = "Get User By Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public UserDto findById(@NonNull @PathVariable Long id) {
        return mapper.toDto(userService.findById(id));
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
        return mapper.toDto(userService.create(mapper.toCreateCommand(command)));
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
        userService.changePassword(id, mapper.toChangePasswordCommand(command));
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


}
