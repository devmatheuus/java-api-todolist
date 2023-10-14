package localhost.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import localhost.todolist.user.IUserRepository;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks")) {
            var authorization = request.getHeader("Authorization");
            var authorizationEncoded = authorization.substring("Basic".length()).trim();

            byte[] authorizationDecoded = Base64.getDecoder().decode(authorizationEncoded);

            var authorizationString = new String(authorizationDecoded);

            String[] credentials = authorizationString.split(":");

            String username = credentials[0];
            String password = credentials[1];

            var user = this.userRepository.findByUsername(username);

            if (user == null) {
                response.sendError(401);
            } else {
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                if (passwordVerify.verified) {
                    request.setAttribute("userId", user.getId());
                    filterChain.doFilter(request, response);

                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
