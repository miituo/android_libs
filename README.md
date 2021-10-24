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
android {
    compileSdkVersion 30
    buildToolsVersion "29.0.3"

    defaultConfig {
        minSdkVersion 21
        targetSdkVersion 30
        ...
        
dependencies {
implementation 'com.github.miituo:android_libs:0.4.0'
...
```
## IMPORTANTE:
La versión 0.4.0 corre apartir del target 30 de android, para versiones anteriores, seguir usando la version 0.3.91

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
            i.putExtra("dev", 0);

            startActivity(i);
        }
    }
});
```  

Donde:
**telefono** es el extra que necesita la librería para buscar las pólizas miituo
**dev** es el bandera para cambiar de ambiente: **1 para desarrollo, 0 para producción**
  
La librería puede:
Reportar odómetro
Toma de fotografías
Ver póliza
  
## Preview:
  
## Autor: 
  
**Pay as you drive technologies**
