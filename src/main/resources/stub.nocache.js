/*
 * This file is part of pwt.
 *
 * pwt is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * pwt is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with pwt. If not,
 * see <http://www.gnu.org/licenses/>.
 */
/**
 * This startup script is used when we run superdevmode from an app server.
 */
(function($wnd, $doc){
  // document.head does not exist in IE8
  var $head = $doc.head || $doc.getElementsByTagName('head')[0];
  // Compute some codeserver urls so as the user does not need bookmarklets
  var hostName = $wnd.location.hostname;
  var serverUrl = 'http://' + hostName + ':__SUPERDEV_PORT__';
  var module = '__MODULE_NAME__';
  var nocacheUrl = serverUrl + '/recompile-requester/' + module;

  // Insert the superdevmode nocache script in the first position of the head
  var devModeScript = $doc.createElement('script');
  devModeScript.src = nocacheUrl;

  // Everybody except IE8 does fire an error event
  // This means that we do not detect a non running SDM with IE8.
  if (devModeScript.addEventListener) {
    var callback = function() {
      // Don't show the confirmation dialogue twice (multimodule)
      if (!$wnd.__gwt__sdm__confirmed &&
           (!$wnd.__gwt_sdm__recompiler || !$wnd.__gwt_sdm__recompiler.loaded)) {
        $wnd.__gwt__sdm__confirmed = true;
        if ($wnd.confirm(
            "Couldn't load " +  module + " from Super Dev Mode\n" +
            "server at " + serverUrl + ".\n" +
            "Please make sure this server is ready.\n" +
            "Do you want to try again?")) {
          $wnd.location.reload();
        }
      }
    };
    devModeScript.addEventListener("error", callback, true);
  }

  var injectScriptTag = function(){
    $head.insertBefore(devModeScript, $head.firstElementChild || $head.children[0]);
  };

  if (/loaded|complete/.test($doc.readyState)) {
    injectScriptTag();
  } else {
    //defer app script insertion until the body is ready
    if($wnd.addEventListener){
      $wnd.addEventListener('load', injectScriptTag, false);
    } else{
      $wnd.attachEvent('onload', injectScriptTag);
    }
  }
})(window, document);
