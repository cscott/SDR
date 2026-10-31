package net.cscott.sdr.webapp.server;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Sets the caching headers GWT's output expects.  The host page and
 * the <code>*.nocache.*</code> loader must be checked on every visit,
 * or browsers keep running an old version of the app after a deploy;
 * the <code>*.cache.*</code> files are named by a hash of their
 * contents, so they can be cached forever.
 */
@SuppressWarnings("serial")
public class CacheControlFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp,
                            FilterChain chain)
        throws IOException, ServletException {
        String path = req.getRequestURI();
        if (path.contains(".nocache.") || path.endsWith(".html") ||
            path.endsWith("/"))
            resp.setHeader("Cache-Control", "no-cache");
        else if (path.contains(".cache."))
            resp.setHeader("Cache-Control",
                           "public, max-age=31536000, immutable");
        chain.doFilter(req, resp);
    }
}
