import {Injectable} from '@angular/core';
import {Router, CanActivate, RouterStateSnapshot, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {UserAuthenticationService} from './UserAuthenticationService';

@Injectable()
export class ActivationGuard implements CanActivate {

    constructor(private router: Router, private userService: UserAuthenticationService) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {


        return this.userService.isUserAuthenticated
            .do(success => {
                if (!success) {
                    this.router.navigate(['/']);
                }
            });

        //return this.userService.isUserAuthenticated
        // .do(success => {
        //     if (!success) {
        //         this.router.navigate(['/']);
        //     }
        // });

        //if (!localStorage.getItem('currentUser')) {
        //    this.router.navigate(['/']);
        //     return false;
        // }
        // return true;
        //}
    }
}