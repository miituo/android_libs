# android_libs
Librería para apps Android

Descripción...

## Instalación

gradle: 
Primeramente agregar las referencias a root build.gradle 
```
allprojects {
    repositories {
        maven {
            url "https://jitpack.io"
            credentials { username authToken }
        }
    }
}
```

Ahora agregamos el token de miituo en gradle.propierties
```
authToken=jp_skpbrvh8a8nnods7876sp6j0bm
```

Finalmente agregamos la dependecia

```
dependencies {
implementation 'com.github.miituo:android_libs:0.1.4'
...
```

## Uso:

Aqui un ejemplo para presentar la vista miituo
  
```
Button buttonLaunch = findViewById(R.id.buttonLaunch);
final EditText editTextPhone = findViewById(R.id.editTextPhone);
buttonLaunch.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(!editTextPhone.getText().toString().equals("")) {
            Intent i = new Intent(MainActivity.this, PrincipalActivity.class);
            i.putExtra("telefono", editTextPhone.getText().toString());
            startActivity(i);
        }
    }
});
```  

Donde telefono es el extra que necesita la librería para buscar las pólizas miituo
  
La librería ya esta capacitada para todo el flujo de reporte, toma de fotografías y resumen
  
## Preview:
  
## Autor: 
  
**Pay as you drive technologies**
