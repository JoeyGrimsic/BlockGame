package org.example;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

public class App {

    private long window;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        init();
        loop();

        // Free resources and close GLFW
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    private void init() {
        // Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Create the window
        window = GLFW.glfwCreateWindow(800, 600, "Rainbow Triangle", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Center the window on primary monitor (optional)
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode != null) {
            GLFW.glfwSetWindowPos(
                window,
                (vidMode.width() - 800) / 2,
                (vidMode.height() - 600) / 2
            );
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);

        // Enable v-sync
        GLFW.glfwSwapInterval(1);

        // Show the window
        GLFW.glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical. It will load all OpenGL functions (via LWJGL)
        GL.createCapabilities();

        // Run until the user wants to close the window
        while (!GLFW.glfwWindowShouldClose(window)) {
            // Poll for window events
            GLFW.glfwPollEvents();

            // Set the clear color to black
            GL11.glClearColor(0f, 0f, 0f, 1f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            // Draw a triangle using immediate mode (legacy style)
            GL11.glBegin(GL11.GL_TRIANGLES);

            // Red vertex
            GL11.glColor3f(1.0f, 0.0f, 0.0f);
            GL11.glVertex2f(-0.5f, -0.5f);

            // Green vertex
            GL11.glColor3f(0.0f, 1.0f, 0.0f);
            GL11.glVertex2f(0.5f, -0.5f);

            // Blue vertex
            GL11.glColor3f(0.0f, 0.0f, 1.0f);
            GL11.glVertex2f(0.0f, 0.5f);

            GL11.glEnd();

            // Swap the color buffers
            GLFW.glfwSwapBuffers(window);
        }
    }
}

