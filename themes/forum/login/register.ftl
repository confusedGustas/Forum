<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm'); section>
    <#if section = "header">
        ${msg("registerTitle")}
    <#elseif section = "form">
        <div id="kc-form">
            <div id="kc-form-wrapper">
                <form id="kc-register-form" action="${url.registrationAction}" method="post">
                    <div class="form-group">
                        <label for="firstName">${msg("firstName")}</label>
                        <input type="text" id="firstName" name="firstName" value="${(register.formData.firstName!'')}" />
                    </div>

                    <div class="form-group">
                        <label for="lastName">${msg("lastName")}</label>
                        <input type="text" id="lastName" name="lastName" value="${(register.formData.lastName!'')}" />
                    </div>

                    <div class="form-group">
                        <label for="email">${msg("email")}</label>
                        <input type="text" id="email" name="email" value="${(register.formData.email!'')}" />
                    </div>

                    <#if !realm.registrationEmailAsUsername>
                        <div class="form-group">
                            <label for="username">${msg("username")}</label>
                            <input type="text" id="username" name="username" value="${(register.formData.username!'')}" />
                        </div>
                    </#if>

                    <div class="form-group">
                        <label for="password">${msg("password")}</label>
                        <input type="password" id="password" name="password" />
                    </div>

                    <div class="form-group">
                        <label for="password-confirm">${msg("passwordConfirm")}</label>
                        <input type="password" id="password-confirm" name="password-confirm" />
                    </div>

                    <div id="kc-form-buttons" class="form-group">
                        <input class="btn" type="submit" value="${msg("doRegister")}"/>
                    </div>
                </form>
            </div>
        </div>

        <div id="kc-registration">
            <span><a href="${url.loginUrl}">${msg("Back to login")}</a></span>
        </div>
    </#if>
</@layout.registrationLayout>
