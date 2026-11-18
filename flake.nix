{
  description = "Servlet Starter 的 Java 25 开发环境";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { nixpkgs, ... }:
    let
      systems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];
    in
    {
      devShells = nixpkgs.lib.genAttrs systems (system:
        let
          pkgs = import nixpkgs { inherit system; };
          jdk = pkgs.temurin-bin-25;
          maven = pkgs.maven.override { jdk_headless = jdk; };
        in
        {
          default = pkgs.mkShell {
            packages = [ jdk maven ];
            JAVA_HOME = jdk.home;
          };
        });
    };
}
