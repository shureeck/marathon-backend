package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class RecipeEntity {
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String tittle;
}
