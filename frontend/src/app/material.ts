import {
    MatInputModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatListModule,
    MatToolbarModule,
    MatDialogModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTooltipModule
} from '@angular/material';
import {
    NgModule
} from '@angular/core'
import {
    FlexLayoutModule
} from "@angular/flex-layout";
@NgModule({
    imports: [MatAutocompleteModule,
        MatButtonModule,
        MatInputModule, MatCardModule, MatIconModule,
        MatListModule, MatToolbarModule, 
        MatDialogModule, MatSelectModule, FlexLayoutModule, MatSnackBarModule,
        MatTooltipModule

    ],

    exports: [MatAutocompleteModule, MatButtonModule,

        MatInputModule, MatCardModule, MatIconModule,
        MatListModule, MatToolbarModule, MatDialogModule, 
        MatSelectModule, FlexLayoutModule, MatSnackBarModule, MatTooltipModule
    ]
})
export class MaterialModule {}