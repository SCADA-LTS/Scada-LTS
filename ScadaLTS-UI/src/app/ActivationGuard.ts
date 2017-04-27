import {Injectable} from '@angular/core';
import {Router, RouterStateSnapshot, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {UserAuthenticationService} from './UserAuthenticationService';

interface CanActivate {
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean
}

@Injectable()
export class ActivationGuard implements CanActivate {

    constructor(private router: Router, private userService: UserAuthenticationService) {
    }

    public canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {

        if (this.userService.isUserAuthenticated == false) {
            this.router.navigate(['/']);
            return false;
        }
        return true;
    }
}