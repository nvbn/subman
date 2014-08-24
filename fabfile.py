from fabric.api import cd, sudo, shell_env, hide


def update():
    with cd('/var/www/subman/'), shell_env(HOME='/var/www/'), hide('stdout', 'stderr'):
        sudo('git pull', user='www-data')
        sudo('rm -rf resources/public/cljs-target')
        sudo('rm -rf target/cljs*')
        sudo('lein with-profile production ring uberjar', user='www-data')
        sudo('supervisorctl restart subman')
