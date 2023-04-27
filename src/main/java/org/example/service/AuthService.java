package org.example.service;


import com.example.grpc.AuthServiceGrpc.AuthServiceImplBase;
import com.example.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.example.util.PasswordUtil;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@GRpcService
public class AuthService extends AuthServiceImplBase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public void signup(SignupRequest request, StreamObserver<SignupResponse> responseObserver) {
        Boolean isEmailExist = userRepository.existsByEmail(request.getEmail());
        if (!isEmailExist) {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordUtil.encode(request.getPassword()));
            User newUser = userRepository.save(user);
            System.out.println(newUser);
            SignupResponse response = SignupResponse.newBuilder().setSuccess(true).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.ALREADY_EXISTS.withDescription("Email already exists").asException());
        }
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        Optional<User> maybeUser = userRepository.findByEmail(request.getEmail());
        if (maybeUser.isEmpty()) {
            responseObserver.onError(
                    Status.UNAUTHENTICATED.withDescription("Incorrect email or password").asException());
        } else {
            User user = maybeUser.get();
            if (passwordUtil.validate(request.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user);
                LoginResponse response = LoginResponse.newBuilder().setToken(token).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(
                        Status.UNAUTHENTICATED.withDescription("Incorrect email or password").asException());
            }
        }
    }

    @Override
    public void validateToken(ValidateRequest request, StreamObserver<ValidateResponse> responseObserver) {
        if (jwtUtil.validateToken(request.getToken())) {
            ValidateResponse response = ValidateResponse.newBuilder().setId(jwtUtil.getId(request.getToken())).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription("Invalid Token").asException());
        }
    }
}