use std::{
    fs::{read_to_string, rename, write, File, OpenOptions},
    io::{self, ErrorKind, Write},
    path::Path,
};

use clap::{Parser, Subcommand};
use copy_dir::copy_dir;

#[derive(Parser)]
#[command(author = "PillowMC", version, about = "Tools for ShearAPI development", long_about = None)]
struct Args {
    #[command(subcommand)]
    command: Commands,
}

#[derive(Subcommand)]
enum Commands {
    /// creates a new module
    NewModule {
        /// The name of the new module.
        name: String,
        /// The description of the new module.
        #[arg(long)]
        desc: Option<String>,
        /// Dependencies of the new module. You do not need to add "shearapi-"
        #[arg(short, long)]
        deps: Vec<String>,
    },
}

fn title(s: String) -> String {
    let mut c = s.chars();
    match c.next() {
        Some(f) => f.to_uppercase().collect::<String>() + c.as_str(),
        None => String::new(),
    }
}

fn new_module(name: String, deps: Vec<String>, desc: String) -> io::Result<()> {
    let modid = format!("shearapi-{}", name);
    let target_dir = "../".to_owned() + &modid;
    let target_dir = Path::new(&target_dir);
    match copy_dir("shearapi-module-template", target_dir) {
        Err(e) => {
            if let ErrorKind::NotFound = e.kind() {
                panic!("Error: shearapi-module-template not found! Are you in tools dir?");
            } else {
                return Err(e);
            }
        }
        Ok(es) => {
            if !es.is_empty() {
                panic!(
                    "Many errors occured: {}",
                    Vec::from_iter(es.iter().map(|e| e.to_string())).join("\n")
                );
            }
        }
    }

    let resources_dir = target_dir.join("src").join("main").join("resources");
    rename(
        resources_dir.join("assets/shearapi-xxx"),
        resources_dir.join("assets").join(modid.clone()),
    )?;

    let fmj = resources_dir.join("fabric.mod.json");
    let fmj = fmj.as_path();
    let mut content = read_to_string(fmj)?
        .replace("shearapi-xxx", modid.as_str())
        .replace("Port NeoForge \\\"API\\\" of xxx to Fabric", &desc)
        .replace("Shear API (XXX)", &format!("Shear API ({})", title(name.clone())));
    if !deps.is_empty() {
        content = content.replace(
            r#""fabric-api": "*""#,
            &(r#""fabric-api": "*""#.to_owned()
                + &Vec::from_iter(deps.iter().map(|dep| {
                    format!(
                        r#",
		"shearapi-{dep}": "~${{version}}""#
                    )
                }))
                .join("")
                .to_owned()),
        );
        let build_gradle = r#"dependencies {
    api project(path: ":shearapi-"#
            .to_owned()
            + &deps
                .join(
                    r#"", configuration: "namedElements")
    api project(path: ":shearapi-"#,
                )
                .to_owned()
            + r#"", configuration: "namedElements")
}"#;
        write(target_dir.join("build.gradle"), build_gradle)?;
    }
    write(fmj, content)?;
    writeln!(OpenOptions::new().append(true).open("../settings.gradle")?, r#"include("shearapi-{}")"#, name)?;
    Ok(())
}

fn main() {
    let args = Args::parse();
    match args.command {
        Commands::NewModule { name, deps, desc } => {
            let desc = match desc {
                Some(d) => d,
                None => "Port NeoForge \\\"API\\\" of xxx to Fabric".replace("xxx", name.as_str()),
            };
            if let Err(e) = new_module(name, deps, desc) {
                panic!("{}", e.to_string());
            }
        }
    }
}
