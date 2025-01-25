package game;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.joml.Math.*;

import java.nio.IntBuffer;

@SuppressWarnings("unused")

public class App {
    private long window;
    private int shaderProgram;
    private int vao;

    public static void main(String[] args) {
        new App().run();
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        // Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure Core Profile
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        // Create window
        window = GLFW.glfwCreateWindow(800, 600, "Core Profile Demo", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        // Center window
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            GLFW.glfwSetWindowPos(
                    window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2);
        }

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(window);
    }

    private void createShaders() {
        // Vertex Shader
        String vertexShaderSource = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "void main() {\n" +
                "   gl_Position = vec4(aPos, 1.0);\n" +
                "}";

        // Fragment Shader
        String fragmentShaderSource = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "uniform vec3 uColor;\n" +
                "void main() {\n" +
                "   FragColor = vec4(uColor, 1.0);\n" +
                "}";

        int vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShader, vertexShaderSource);
        GL20.glCompileShader(vertexShader);

        int fragmentShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShader, fragmentShaderSource);
        GL20.glCompileShader(fragmentShader);

        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragmentShader);
        GL20.glLinkProgram(shaderProgram);

        // Cleanup shaders
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    private void setupVertexData() {
        float[] vertices = {
                -0.5f, -0.5f, 0.0f, // Left
                0.5f, -0.5f, 0.0f, // Right
                0.0f, 0.5f, 0.0f // Top
        };

        vao = GL30.glGenVertexArrays();
        int vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        GL20.glEnableVertexAttribArray(0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void loop() {
        GL.createCapabilities();
        createShaders();
        setupVertexData();

        while (!GLFW.glfwWindowShouldClose(window)) {
            float time = (float) GLFW.glfwGetTime();

            // Update uniform color
            float red = abs(sin(time));
            float green = abs(sin(time * 0.33f));
            float blue = abs(sin(time * 0.66f));

            // Clear screen
            GL11.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            // Draw triangle
            GL20.glUseProgram(shaderProgram);
            GL20.glUniform3f(GL20.glGetUniformLocation(shaderProgram, "uColor"), red, green, blue);

            GL30.glBindVertexArray(vao);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void cleanup() {
        // Cleanup OpenGL resources
        GL30.glDeleteVertexArrays(vao);
        GL20.glDeleteProgram(shaderProgram);

        // Cleanup GLFW
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }
}
