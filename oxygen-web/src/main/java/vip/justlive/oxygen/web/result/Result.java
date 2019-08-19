/*
 *  Copyright (C) 2019 justlive1
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License
 *  is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing permissions and limitations under
 *  the License.
 */
package vip.justlive.oxygen.web.result;

import vip.justlive.oxygen.web.http.Request;

/**
 * 结果标记
 *
 * @author wubo
 */
public interface Result {

  /**
   * redirect result
   *
   * @param url redirect url
   * @return Result
   */
  static RedirectResult redirect(String url) {
    return new RedirectResult(url);
  }

  /**
   * view result
   *
   * @param path 路径
   * @return Result
   */
  static ViewResult view(String path) {
    ViewResult result = new ViewResult();
    result.setPath(path);
    result.addAttribute("ctx", Request.current().getContextPath());
    return result;
  }

  /**
   * json result
   *
   * @param data 数据
   * @return Result
   */
  static JsonResult json(Object data) {
    return new JsonResult(data);
  }
}