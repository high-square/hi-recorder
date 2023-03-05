package highsquare.hirecoder.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ScriptUtils {

    public static void init(HttpServletResponse response) {
        response.setContentType("text/html; charset=euc-kr");
        response.setCharacterEncoding("euc-kr");
    }

    public static void alert(HttpServletResponse response, String alertText) throws IOException {
        init(response);
        PrintWriter out = response.getWriter();
        out.println("<script>alert('" + alertText + "');</script> ");
        out.flush();
    }

    public static void alertAndMovePage(HttpServletResponse response, String alertText, String nextPage)
    throws IOException {
        init(response);
        PrintWriter out = response.getWriter();
        out.println("<script>alert('" + alertText + "'); location.href='" + nextPage + "';</script>");
        out.flush();
    }

    public static void alertAndMovePagePost(HttpServletResponse response, String alertText, String nextPage)
        throws IOException {
        init(response);
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML>"+"<html>"+"<body>"+
                "<script>alert('"+alertText+"'); let form = document.createElement('form');" +
                "form.setAttribute('method','post');" +
                "form.setAttribute('action','"+nextPage+"');"+
                "document.body.appendChild(form);" +
                "form.submit();</script>"+
                "</body>"+
                "</html>");
        out.flush();
    }

    public static void alertAndBackPage(HttpServletResponse response, String alertText) throws IOException {
        init(response);
        PrintWriter out = response.getWriter();
        out.println("<script>alert('"+alertText+"'); history.go(-1);</script>");
        out.flush();
    }
}
