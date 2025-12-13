package com.example.lab5.ui;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Lightweight Material Design inspired UI served directly from the backend.
 * The page bundles a few helper widgets for registration, authentication and
 * basic function/point creation so the manual API is immediately usable
 * without external tools.
 */
@WebServlet("/ui")
public class UiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");

        String context = req.getContextPath();
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Lab7 UI</title>");
            out.println("<link href='https://fonts.googleapis.com/icon?family=Material+Icons' rel='stylesheet'>");
            out.println("<link href='https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css' rel='stylesheet'>");
            out.println("<style>body{background:#121212;color:#e0e0e0;} .card{background:#1e1e1e;} .tabs .tab a{color:#90caf9;} .tabs .tab a.active{color:#42a5f5;} .tabs .indicator{background:#42a5f5;} textarea{color:#e0e0e0;} .error-chip{background:#ef5350;color:white;} .success-chip{background:#66bb6a;color:#0b1;} .monospace{font-family:'Roboto Mono',monospace;}</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<nav class='z-depth-0' style='background:#1e1e1e;padding:0 20px;'>");
            out.println("  <div class='nav-wrapper'><a href='#' class='brand-logo'>Lab7 Material UI</a></div>");
            out.println("</nav>");
            out.println("<div class='container' style='margin-top:20px;'>");
            out.println("  <ul id='ui-tabs' class='tabs tabs-fixed-width z-depth-1'>");
            out.println("    <li class='tab col s3'><a href='#auth-panel' class='active'>Авторизация</a></li>");
            out.println("    <li class='tab col s3'><a href='#functions-panel'>Функции (массивы)</a></li>");
            out.println("    <li class='tab col s3'><a href='#simple-fn-panel'>Функции (простые)</a></li>");
            out.println("  </ul>");

            // Auth card
            out.println("  <div id='auth-panel' class='card z-depth-1' style='padding:20px;margin-top:12px;'>");
            out.println("    <h5>Регистрация и настройка Basic Auth</h5>");
            out.println("    <div class='row'>");
            out.println("      <form id='register-form' class='col s12'>");
            out.println("        <div class='row'>");
            out.println("          <div class='input-field col s4'><input id='reg-login' required><label for='reg-login'>Логин</label></div>");
            out.println("          <div class='input-field col s4'><input id='reg-password' type='password' required><label for='reg-password'>Пароль</label></div>");
            out.println("          <div class='input-field col s4'><input id='reg-role' value='USER'><label for='reg-role'>Роль (USER/ADMIN)</label></div>");
            out.println("        </div>");
            out.println("        <button class='btn waves-effect waves-light blue' type='submit'>Создать пользователя</button>");
            out.println("      </form>");
            out.println("    </div>");
            out.println("    <div class='row' style='margin-top:20px;'>");
            out.println("      <form id='auth-form' class='col s12'>");
            out.println("        <div class='row'>");
            out.println("          <div class='input-field col s6'><input id='auth-login'><label for='auth-login'>Логин для Basic Auth</label></div>");
            out.println("          <div class='input-field col s6'><input id='auth-password' type='password'><label for='auth-password'>Пароль</label></div>");
            out.println("        </div>");
            out.println("        <button class='btn waves-effect waves-light green' type='submit'>Сохранить креды</button>");
            out.println("      </form>");
            out.println("    </div>");
            out.println("    <div id='auth-log' class='monospace'></div>");
            out.println("  </div>");

            // Array based functions
            out.println("  <div id='functions-panel' class='card z-depth-1' style='padding:20px;margin-top:12px;'>");
            out.println("    <h5>Создание функции из массивов x/y</h5>");
            out.println("    <div class='row'>");
            out.println("      <form id='array-function-form' class='col s12'>");
            out.println("        <div class='row'>");
            out.println("          <div class='input-field col s4'><input id='fn-user-id' type='number' min='1' required><label for='fn-user-id'>ID пользователя</label></div>");
            out.println("          <div class='input-field col s4'><input id='fn-name' required><label for='fn-name'>Имя функции</label></div>");
            out.println("          <div class='input-field col s4'><input id='point-count' type='number' min='2' max='100' value='3'><label for='point-count'>Количество точек</label></div>");
            out.println("        </div>");
            out.println("        <div class='row'><div class='col s12'><table class='striped highlight' id='points-table'><thead><tr><th>x</th><th>y</th></tr></thead><tbody></tbody></table></div></div>");
            out.println("        <div class='row'><button class='btn blue waves-effect waves-light' type='submit'>Создать и сохранить</button></div>");
            out.println("      </form>");
            out.println("    </div>");
            out.println("    <div id='array-log' class='monospace'></div>");
            out.println("  </div>");

            // Simple functions via dropdown
            out.println("  <div id='simple-fn-panel' class='card z-depth-1' style='padding:20px;margin-top:12px;'>");
            out.println("    <h5>Создание функции на основе простой MathFunction</h5>");
            out.println("    <p>Выберите шаблон и диапазон, после чего функция будет создана и табулирована с равным шагом.</p>");
            out.println("    <form id='simple-function-form'>");
            out.println("      <div class='row'>");
            out.println("        <div class='input-field col s3'><input id='simple-user-id' type='number' min='1' required><label for='simple-user-id'>ID пользователя</label></div>");
            out.println("        <div class='input-field col s3'><input id='simple-name' required><label for='simple-name'>Имя функции</label></div>");
            out.println("        <div class='input-field col s3'><input id='simple-count' type='number' min='2' value='5'><label for='simple-count'>Точек</label></div>");
            out.println("        <div class='input-field col s3'><select id='simple-kind'><option value='identity'>Тождественная</option><option value='square'>Квадратичная</option><option value='sin'>Синус</option><option value='cos'>Косинус</option></select><label>Базовая функция</label></div>");
            out.println("      </div>");
            out.println("      <div class='row'>");
            out.println("        <div class='input-field col s4'><input id='simple-start' type='number' step='0.1' value='0'><label for='simple-start'>Начало интервала</label></div>");
            out.println("        <div class='input-field col s4'><input id='simple-end' type='number' step='0.1' value='10'><label for='simple-end'>Конец интервала</label></div>");
            out.println("        <div class='input-field col s4'><input id='simple-signature' value='ui-generated'><label for='simple-signature'>Подпись</label></div>");
            out.println("      </div>");
            out.println("      <button class='btn purple waves-effect waves-light' type='submit'>Создать</button>");
            out.println("    </form>");
            out.println("    <div id='simple-log' class='monospace' style='margin-top:10px;'></div>");
            out.println("  </div>");

            out.println("</div>");
            out.println("<div id='alert-modal' class='modal'><div class='modal-content'><h5 id='alert-title'></h5><p id='alert-body'></p></div><div class='modal-footer'><a href='#!' class='modal-close waves-effect waves-green btn-flat'>Закрыть</a></div></div>");
            out.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js'></script>");
            out.println("<script>");
            out.println("document.addEventListener('DOMContentLoaded', () => {M.AutoInit(); rebuildRows();});");
            out.println("const apiBase = '" + context + "';");
            out.println("let authHeader = '';");

            // helper display
            out.println("function log(target, message, success=true){const el=document.getElementById(target);const cls=success?'success-chip':'error-chip';el.innerHTML= `<div class='chip ${cls}'>${message}</div>`;}");
            out.println("function openModal(title, body){document.getElementById('alert-title').innerText=title;document.getElementById('alert-body').innerText=body;const m=M.Modal.getInstance(document.getElementById('alert-modal'))||M.Modal.init(document.getElementById('alert-modal'));m.open();}");

            // registration
            out.println("document.getElementById('register-form').addEventListener('submit', async (e)=>{e.preventDefault();try{const payload={login:reg-login.value,password:reg-password.value,role:reg-role.value||'USER'};const res=await fetch(`${apiBase}/users`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)});const data=await res.json();if(res.ok){log('auth-log','Пользователь создан: '+JSON.stringify(data));}else{openModal('Ошибка регистрации', JSON.stringify(data));}}catch(err){openModal('Ошибка сети', err.message);}});");

            // auth
            out.println("document.getElementById('auth-form').addEventListener('submit',(e)=>{e.preventDefault();const login=document.getElementById('auth-login').value;const pass=document.getElementById('auth-password').value;authHeader='Basic '+btoa(login+':'+pass);log('auth-log','Basic Auth сохранен для '+login);});");

            // rebuild point rows
            out.println("function rebuildRows(){const tbody=document.querySelector('#points-table tbody');tbody.innerHTML='';const count=parseInt(document.getElementById('point-count').value||'0');for(let i=0;i<count;i++){const tr=document.createElement('tr');tr.innerHTML=`<td><input class='white-text' name='x${i}' type='number' step='0.1'></td><td><input class='white-text' name='y${i}' type='number' step='0.1'></td>`;tbody.appendChild(tr);}}");
            out.println("document.getElementById('point-count').addEventListener('change', rebuildRows);");

            // array-based creation
            out.println("document.getElementById('array-function-form').addEventListener('submit', async (e)=>{e.preventDefault();const userId=document.getElementById('fn-user-id').value;const name=document.getElementById('fn-name').value;const signature='tabulated-array-ui';try{const fnRes=await fetch(`${apiBase}/functions`,{method:'POST',headers:{'Content-Type':'application/json','Authorization':authHeader},body:JSON.stringify({userId:Number(userId),name,signature})});const fnData=await fnRes.json();if(!fnRes.ok){openModal('Ошибка создания функции', JSON.stringify(fnData));return;}const fnId=fnData.id || fnData.data || fnData.functionId || fnData;const inputs=document.querySelectorAll('#points-table tbody input');for(let i=0;i<inputs.length;i+=2){const x=inputs[i].value;const y=inputs[i+1].value;if(x===''||y==='') continue;const ptRes=await fetch(`${apiBase}/points`,{method:'POST',headers:{'Content-Type':'application/json','Authorization':authHeader},body:JSON.stringify({functionId:fnId,xValue:Number(x),yValue:Number(y)})});if(!ptRes.ok){const pData=await ptRes.json();openModal('Не удалось сохранить точку', JSON.stringify(pData));return;}}log('array-log','Функция '+name+' создана с ID '+fnId);}
            catch(err){openModal('Ошибка сети', err.message);} });");

            // simple function creation
            out.println("document.getElementById('simple-function-form').addEventListener('submit', async (e)=>{e.preventDefault();const userId=Number(document.getElementById('simple-user-id').value);const name=document.getElementById('simple-name').value;const count=Number(document.getElementById('simple-count').value);const start=Number(document.getElementById('simple-start').value);const end=Number(document.getElementById('simple-end').value);const kind=document.getElementById('simple-kind').value;const signature=document.getElementById('simple-signature').value||'ui-generated';const step=(end-start)/(count-1);try{const fnRes=await fetch(`${apiBase}/functions`,{method:'POST',headers:{'Content-Type':'application/json','Authorization':authHeader},body:JSON.stringify({userId,name,signature})});const fnData=await fnRes.json();if(!fnRes.ok){openModal('Ошибка создания функции', JSON.stringify(fnData));return;}const fnId=fnData.id || fnData.data || fnData;let generator;switch(kind){case 'square':generator=(x)=>x*x;break;case 'sin':generator=(x)=>Math.sin(x);break;case 'cos':generator=(x)=>Math.cos(x);break;default:generator=(x)=>x;}
            for(let i=0;i<count;i++){const x=start+step*i;const y=generator(x);const ptRes=await fetch(`${apiBase}/points`,{method:'POST',headers:{'Content-Type':'application/json','Authorization':authHeader},body:JSON.stringify({functionId:fnId,xValue:x,yValue:y})});if(!ptRes.ok){const pData=await ptRes.json();openModal('Ошибка сохранения точки', JSON.stringify(pData));return;}}
            log('simple-log',`Функция ${name} создана (${kind}) ID=${fnId}`);
            }catch(err){openModal('Ошибка сети', err.message);} });");
            out.println("</script>");
            out.println("</body></html>");
        }
    }
}

