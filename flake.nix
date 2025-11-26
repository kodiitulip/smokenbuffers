{
  description = "Minecraft (Neo)Forge mod development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = nixpkgs.legacyPackages.${system};

        libraryInclde = with pkgs; [
          glfw3-minecraft
          openal
          alsa-lib
          libjack2
          libpulseaudio
          pipewire

          libGL
          xorg.libX11
          xorg.libXext
          xorg.libXcursor
          xorg.libXrandr
          xorg.libXxf86vm
          flite
        ];
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs =
            with pkgs;
            [
              # Java 21 (Temurin)
              temurin-bin-21
              gradle

              # Build tools
              git
              unzip
              wget
            ]
            ++ libraryInclde;

          shellHook = ''
            echo "Minecraft (Neo)Forge mod development environment"
            echo "Java: $(java --version)"
            echo "Gradle: $(gradle --version | grep Gradle)"
          '';

          env = {
            LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath libraryInclde;
            GLFW_PLATFORM = "x11";
            JAVA_HOME = pkgs.temurin-bin-21;
          };
        };
      }
    );
}
