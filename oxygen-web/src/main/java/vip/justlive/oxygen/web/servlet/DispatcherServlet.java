/*
 * Copyright (C) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package vip.justlive.oxygen.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import vip.justlive.oxygen.core.net.http.HttpMethod;
import vip.justlive.oxygen.core.util.Strings;
import vip.justlive.oxygen.web.Context;
import vip.justlive.oxygen.web.http.Cookie;
import vip.justlive.oxygen.web.http.Request;
import vip.justlive.oxygen.web.http.Response;
import vip.justlive.oxygen.web.result.JspViewResultHandler;
import vip.justlive.oxygen.web.result.Result;
import vip.justlive.oxygen.web.result.ResultHandler;
import vip.justlive.oxygen.web.router.RouteHandler;
import vip.justlive.oxygen.web.router.RoutingContext;
import vip.justlive.oxygen.web.router.RoutingContextImpl;

/**
 * 请求分发器
 *
 * @author wubo
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private void doService(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod) {
    if (log.isDebugEnabled()) {
      log.debug("DispatcherServlet accept request for [{}] on method [{}]", req.getServletPath(),
          httpMethod);
    }

    String requestUri = req.getRequestURI();
    String queryString = req.getQueryString();
    if (queryString != null && queryString.length() > 0) {
      requestUri += Strings.QUESTION_MARK + queryString;
    }

    final Request request = new Request(httpMethod, requestUri, req.getProtocol());
    request.addAttribute(Request.ORIGINAL_REQUEST, req);
    request.addAttribute(Response.ORIGINAL_RESPONSE, resp).local();
    final Response response = new Response(request);
    response.local();
    final RoutingContext ctx = new RoutingContextImpl(request, response);
    try {
      Context.parseRequest(request);
      RouteHandler handler = request.getRouteHandler();
      if (handler == null) {
        RouteHandler.notFound(ctx);
        return;
      }
      if (Context.invokeBefore(ctx)) {
        handler.handle(ctx);
        handleResult(ctx, response.getResult(), resp);
        Context.invokeAfter(ctx);
      }
    } catch (Exception e) {
      RouteHandler.error(ctx, e);
    } finally {
      Context.invokeFinished(ctx);
      copyResponse(request, response, resp);
      copyStream(response, resp);
      Request.clear();
      Response.clear();
    }
  }

  private void handleResult(RoutingContext ctx, Result result, HttpServletResponse resp) {
    for (ResultHandler handler : Context.HANDLERS) {
      if (handler.support(result)) {
        if (handler instanceof JspViewResultHandler) {
          copyResponse(ctx.request(), ctx.response(), resp);
        }
        handler.apply(ctx, result);
        break;
      }
    }
  }

  private void copyResponse(Request request, Response response, HttpServletResponse resp) {
    if (response.getContentType() != null) {
      resp.setContentType(response.getContentType());
    }
    resp.setStatus(response.getStatus());
    resp.setCharacterEncoding(response.getEncoding());
    response.getHeaders().forEach(resp::addHeader);

    Context.restoreSession(request, response);

    for (Cookie cookie : response.getCookies().values()) {
      javax.servlet.http.Cookie jCookie = new javax.servlet.http.Cookie(cookie.getName(),
          cookie.getValue());
      jCookie.setPath(cookie.getPath());
      jCookie.setSecure(cookie.isSecure());
      if (cookie.getMaxAge() != null) {
        jCookie.setMaxAge(cookie.getMaxAge());
      }
      if (cookie.getDomain() != null) {
        jCookie.setDomain(cookie.getDomain());
      }
      resp.addCookie(jCookie);
    }
  }

  private void copyStream(Response response, HttpServletResponse resp) {
    if (response.getOut().size() > 0) {
      try {
        resp.getOutputStream().write(response.getOut().toByteArray());
      } catch (IOException e) {
        log.error("write outputStream error", e);
      }
    }
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (HttpMethod.PATCH.name().equals(req.getMethod())) {
      doService(req, resp, HttpMethod.PATCH);
    } else {
      super.service(req, resp);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doService(req, resp, HttpMethod.GET);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    doService(req, resp, HttpMethod.POST);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
    doService(req, resp, HttpMethod.DELETE);
  }

  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp) {
    doService(req, resp, HttpMethod.HEAD);
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
    doService(req, resp, HttpMethod.PUT);
  }

  @Override
  protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
    doService(req, resp, HttpMethod.OPTIONS);
  }

  @Override
  protected void doTrace(HttpServletRequest req, HttpServletResponse resp) {
    doService(req, resp, HttpMethod.TRACE);
  }

}
