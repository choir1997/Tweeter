package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class RegisterPresenter extends LoginRegisterPresenter {
    private final LoginRegisterView view;
    private final UserService userService;

    public RegisterPresenter(LoginRegisterView view) {
        super(view);
        this.view = view;
        userService = new UserService();
    }

    public void validateAndRegister(String firstName, String lastName, String alias, String password, Bitmap image) {
        try {
            validateRegistration(firstName, lastName, alias, password, image);
            view.setErrorView(null);
            view.displayMessage("Registering...");

            // Convert image to byte array.
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();

            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

            // Send register request.
            userService.runRegister(firstName, lastName, alias, password, imageBytesBase64, new GetLoginRegisterObserver("Failed to register: ", "Failed to register because of exception: "));

        } catch (Exception e) {
            view.setErrorView(e.getMessage());
            Log.e(LogTag.REGISTER_LOG_TAG, e.getMessage(), e);
        }
    }

    public void validateRegistration(String firstName, String lastName, String alias, String password, Bitmap imageToUpload) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (imageToUpload == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
        checkAliasAndPassword(alias, password);
    }
}
