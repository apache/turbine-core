package org.apache.turbine.util.hibernate;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;




public class HibernateFilter implements Filter
{
    //~ Static fields/initializers =============================================

    //~ Instance fields ========================================================

    /**
     * The <code>Log</code> instance for this class
     */
    private Log log = LogFactory.getLog(HibernateFilter.class);
    private FilterConfig filterConfig = null;

    //~ Methods ================================================================

    public void init(FilterConfig filterConfig) throws ServletException
    {
        this.filterConfig = filterConfig;

    }

    /**
     * Destroys the filter.
     */
    public void destroy()
    {
        filterConfig = null;
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
    {
        // cast to the types I want to use
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpSession session = request.getSession(true);

        Session ses = null;
        boolean sessionCreated = false;

        try
        {
            chain.doFilter(request, response);
        }
        finally
        {
            try
            {
                HibernateUtils.closeSession();
            }
            catch (Exception exc)
            {
                log.error("Error closing hibernate session.", exc);
                exc.printStackTrace();
            }
        }
    }

    public static Session getSession() throws PersistenceException
    {
        try
        {

            return HibernateUtils.currentSession();
        }
        catch (Exception e)
        {
            throw new PersistenceException("Could not find current Hibernate session.", e);
        }

    }
}
